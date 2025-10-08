package main

import (
	"encoding/json"
	"fmt"
	"math"
	"os"
	"strconv"
	"time"
)

type Stats struct {
	GameTime       int    `json:"gameTime"`
	DoesRedWin     bool   `json:"doesRedWin"`
	RedDeepness    int    `json:"redDeepness"`
	YellowDeepness int    `json:"yellowDeepness"`
	Moves          []Move `json:"moves"`
}

type Move struct {
	Time       int         `json:"time"`
	IsRedTurn  bool        `json:"isRedTurn"`
	Coordinate *Coordinate `json:"coordinate"`
}

type Coordinate struct {
	X int `json:"x"`
	Y int `json:"y"`
}

type CoordinateToValue struct {
	coordinate *Coordinate
	value      int
}

func main() {
	if len(os.Args) < 3 {
		fmt.Println("Usage: connect4-bot-go <redDeepness> <yellowDeepness>")
		os.Exit(1)
	}

	redDeepness, err1 := strconv.Atoi(os.Args[1])
	yellowDeepness, err2 := strconv.Atoi(os.Args[2])

	if err1 != nil || err2 != nil {
		fmt.Println("Invalid arguments")
		os.Exit(1)
	}

	var board = [][]int{
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0},
	}

	isRedTurn := true
	round := 0

	moves := make([]Move, 0)

	stats := Stats{
		GameTime:       0,
		DoesRedWin:     false,
		RedDeepness:    redDeepness,
		YellowDeepness: yellowDeepness,
		Moves:          moves,
	}

	start := time.Now()
	for {
		round++
		var piece int
		if isRedTurn {
			piece = 1
		} else {
			piece = 2
		}

		if len(getPossibleMoveCoordinates(board)) == 0 {
			break
		}

		var coordinate *Coordinate

		startGettingCoordinate := time.Now()
		if isRedTurn {
			coordinate = getMinmax(board, redDeepness, true, 1).coordinate
		} else {
			coordinate = getMinmax(board, yellowDeepness, true, 2).coordinate
		}

		moves = append(moves, Move{int(time.Since(startGettingCoordinate).Milliseconds()), isRedTurn, coordinate})
		stats.Moves = moves

		possibleMovesCoordinates := getPossibleMoveCoordinates(board)
		isValid := false
		for _, possibleMoveCoordinate := range possibleMovesCoordinates {
			if possibleMoveCoordinate.X == coordinate.X && possibleMoveCoordinate.Y == coordinate.Y {
				isValid = true
				break
			}
		}

		if !isValid {
			break
		}

		board[coordinate.Y][coordinate.X] = piece

		if doesPlayerWin(board, coordinate, piece) {
			break
		}

		isRedTurn = !isRedTurn
	}

	stats.GameTime = int(time.Since(start).Milliseconds())
	stats.DoesRedWin = isRedTurn

	statsJson, err := json.MarshalIndent(stats, "", " ")

	if err != nil {
		fmt.Println(err)
	}

	fmt.Println(string(statsJson))
}

func getPossibleMoveCoordinates(board [][]int) []*Coordinate {
	var possibleMoves []*Coordinate

	for x := 0; x < len(board[0]); x++ {
		for y := 0; y < len(board); y++ {
			if board[y][x] == 0 {
				possibleMoves = append(possibleMoves, &Coordinate{x, y})
				break
			}
		}
	}

	return possibleMoves
}

func checkDiscsOnARow(board [][]int, coordinate *Coordinate, piece int) int {
	directions := [][]int{{0, 1}, {1, 0}, {1, 1}, {1, -1}}
	count := 0

	for _, direction := range directions {
		total := 1 + countInDirection(board, coordinate, direction[0], direction[1], piece) + countInDirection(board, coordinate, -direction[0], -direction[1], piece)
		if total > count {
			count = total
		}
	}

	return count
}

func countInDirection(board [][]int, coordinate *Coordinate, directionX int, directionY int, piece int) int {
	y := coordinate.Y + directionY
	x := coordinate.X + directionX
	count := 0

	for y >= 0 && y < len(board) && x >= 0 && x < len(board[y]) && board[y][x] == piece {
		count++
		y += directionY
		x += directionX
	}

	return count
}

func doesPlayerWin(board [][]int, coordinate *Coordinate, piece int) bool {
	return checkDiscsOnARow(board, coordinate, piece) >= 4
}

func getMinmax(board [][]int, depth int, maximizingPlayer bool, piece int) CoordinateToValue {
	possibleMovesCoordinates := getPossibleMoveCoordinates(board)

	var oppPiece int
	if piece == 1 {
		oppPiece = 2
	} else {
		oppPiece = 1
	}

	if depth == 0 || isTerminal(board) {
		if isTerminal(board) {
			if winningMove(board, piece) {
				return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: math.MaxInt}
			} else if winningMove(board, oppPiece) {
				return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: math.MinInt}
			} else {
				return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: 0}
			}
		} else {
			return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: scorePosition(board, piece)}
		}
	}

	if maximizingPlayer {
		value := math.MinInt
		var bestCoordinate Coordinate

		for _, coordinate := range possibleMovesCoordinates {
			fakeBoard := make([][]int, len(board))
			for i := range board {
				fakeBoard[i] = make([]int, len(board[i]))
				copy(fakeBoard[i], board[i])
			}
			fakeBoard[coordinate.Y][coordinate.X] = piece

			newScore := getMinmax(fakeBoard, depth-1, false, oppPiece).value

			if newScore > value {
				value = newScore
				bestCoordinate = Coordinate{coordinate.X, coordinate.Y}
			}
		}

		return CoordinateToValue{coordinate: &bestCoordinate, value: value}
	} else {
		value := math.MaxInt
		var bestCoordinate Coordinate

		for _, coordinate := range possibleMovesCoordinates {
			fakeBoard := make([][]int, len(board))
			for i := range board {
				fakeBoard[i] = make([]int, len(board[i]))
				copy(fakeBoard[i], board[i])
			}
			fakeBoard[coordinate.Y][coordinate.X] = piece

			newScore := getMinmax(fakeBoard, depth-1, true, oppPiece).value

			if newScore < value {
				value = newScore
				bestCoordinate = Coordinate{coordinate.X, coordinate.Y}
			}
		}

		return CoordinateToValue{coordinate: &bestCoordinate, value: value}
	}
}

func isTerminal(board [][]int) bool {
	return len(getPossibleMoveCoordinates(board)) == 0 || winningMove(board, 1) || winningMove(board, 2)
}

func winningMove(board [][]int, piece int) bool {
	for y, col := range board {
		for x, value := range col {
			if piece != value {
				continue
			}

			if checkDiscsOnARow(board, &Coordinate{x, y}, value) >= 4 {
				return true
			}
		}
	}

	return false
}

func scorePosition(board [][]int, piece int) int {
	score := 0

	score += checkCenter(board, piece)
	score += checkHorizontal(board, piece)
	score += checkVertical(board, piece)
	score += checkPositiveDiagonal(board, piece)
	score += checkNegativeDiagonal(board, piece)

	return score
}

func evaluateWindow(window []int, piece int) int {
	score := 0

	var oppPiece int = 2
	if piece == 2 {
		oppPiece = 1
	}

	countPiece := 0
	for _, value := range window {
		if value == piece {
			countPiece++
		}
	}

	countEmpty := 0
	for _, value := range window {
		if value == 0 {
			countEmpty++
		}
	}

	countOppPiece := 0
	for _, value := range window {
		if value == oppPiece {
			countOppPiece++
		}
	}

	if countPiece == 4 {
		score += 100
	} else if countPiece == 3 && countEmpty == 1 {
		score += 5
	} else if countPiece == 2 && countEmpty == 2 {
		score += 2
	}

	if countOppPiece == 4 {
		score -= 100
	} else if countOppPiece == 3 && countEmpty == 1 {
		score -= 5
	} else if countOppPiece == 2 && countEmpty == 2 {
		score -= 2
	}
	return score
}

func checkCenter(board [][]int, piece int) int {
	score := 0
	yCount := len(board)
	xCount := len(board[0])

	centreCol := xCount / 2
	centreArray := make([]int, yCount)

	for y := 0; y < yCount; y++ {
		centreArray[y] = board[y][centreCol]
	}

	for _, value := range centreArray {
		if value == piece {
			score++
		}
	}

	return score * 3
}

func checkHorizontal(board [][]int, piece int) int {
	score := 0
	yCount := len(board)
	xCount := len(board[0])
	windowLength := 4

	for y := 0; y < yCount; y++ {
		yArray := make([]int, xCount)
		copy(yArray, board[y])

		for x := 0; x < xCount-3; x++ {
			window := yArray[x : x+windowLength]
			score += evaluateWindow(window, piece)
		}
	}

	return score
}

func checkVertical(board [][]int, piece int) int {
	score := 0
	yCount := len(board)
	xCount := len(board[0])
	windowLength := 4

	for x := 0; x < xCount; x++ {
		xArray := make([]int, xCount)
		for y := 0; y < yCount; y++ {
			xArray[y] = board[y][x]
		}

		for y := 0; y < yCount-3; y++ {
			window := xArray[y : y+windowLength]
			score += evaluateWindow(window, piece)
		}
	}

	return score
}

func checkPositiveDiagonal(board [][]int, piece int) int {
	score := 0
	yCount := len(board)
	xCount := len(board[0])
	windowLength := 4

	for x := 0; x < xCount-3; x++ {
		for y := 0; y < yCount-3; y++ {
			window := make([]int, windowLength)
			for i := 0; i < windowLength; i++ {
				window[i] = board[y+i][x+i]
			}
			score += evaluateWindow(window, piece)
		}
	}

	return score
}

func checkNegativeDiagonal(board [][]int, piece int) int {
	score := 0
	yCount := len(board)
	xCount := len(board[0])
	windowLength := 4

	for x := 0; x < xCount-3; x++ {
		for y := 0; y < yCount-3; y++ {
			window := make([]int, windowLength)
			for i := 0; i < windowLength; i++ {
				window[i] = board[y+3-i][x+i]
			}
			score += evaluateWindow(window, piece)
		}
	}

	return score
}
