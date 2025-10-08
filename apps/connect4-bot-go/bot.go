package main

//func getMinmax(board [][]int, depth int, maximizingPlayer bool, piece int) CoordinateToValue {
//	possibleMovesCoordinates := getPossibleMoveCoordinates()
//
//	var invertedPiece int
//	if piece == 1 {
//		invertedPiece = 2
//	} else {
//		invertedPiece = 1
//	}
//
//	if depth == 0 || isTerminal(board) {
//		if depth == 0 {
//			return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: scorePosition(board, piece)}
//		}
//
//		if winningMove(board, piece) {
//			return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: math.MaxInt}
//		} else if winningMove(board, invertedPiece) {
//			return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: math.MaxInt}
//		} else {
//			return CoordinateToValue{coordinate: &Coordinate{0, 0}, value: 0}
//		}
//	}
//
//	if maximizingPlayer {
//		value := math.MinInt
//		var bestCoordinate Coordinate
//
//		for _, coordinate := range possibleMovesCoordinates {
//			fakeBoard := board
//			fakeBoard[coordinate.y][coordinate.x] = piece
//
//			var newPiece int
//			if piece == 1 {
//				newPiece = 2
//			} else {
//				newPiece = 1
//			}
//
//			newScore := getMinmax(board, depth-1, false, newPiece).value
//
//			if newScore > value {
//				value = newScore
//				bestCoordinate = Coordinate{coordinate.x, coordinate.y}
//			}
//		}
//
//		return CoordinateToValue{coordinate: &bestCoordinate, value: value}
//	} else {
//		value := math.MaxInt
//		var bestCoordinate Coordinate
//
//		for _, coordinate := range possibleMovesCoordinates {
//			fakeBoard := board
//			fakeBoard[coordinate.y][coordinate.x] = piece
//
//			var newPiece int
//			if piece == 1 {
//				newPiece = 2
//			} else {
//				newPiece = 1
//			}
//
//			newScore := getMinmax(board, depth-1, true, newPiece).value
//
//			if newScore < value {
//				value = newScore
//				bestCoordinate = Coordinate{coordinate.x, coordinate.y}
//			}
//		}
//
//		return CoordinateToValue{coordinate: &bestCoordinate, value: value}
//	}
//}
//
//func isTerminal(board [][]int) bool {
//	return len(getPossibleMoveCoordinates()) == 0 || winningMove(board, 1) || winningMove(board, 2)
//}
//
//func scorePosition(board [][]int, piece int) int {
//	score := 0
//
//	score += checkCenter(board, piece)
//	score += checkHorizontal(board, piece)
//	score += checkVertical(board, piece)
//	score += checkPositiveDiagonal(board, piece)
//	score += checkNegativeDiagonal(board, piece)
//
//	return score
//}
//
//func evaluateWindow(window []int, piece int) int {
//	score := 0
//
//	var oppPiece int = 2
//	if piece == 2 {
//		oppPiece = 1
//	}
//
//	countPiece := 0
//	for _, value := range window {
//		if value == piece {
//			countPiece++
//		}
//	}
//
//	countEmpty := 0
//	for _, value := range window {
//		if value == 0 {
//			countEmpty++
//		}
//	}
//
//	countOppPiece := 0
//	for _, value := range window {
//		if value == oppPiece {
//			countOppPiece++
//		}
//	}
//
//	if countPiece == 4 {
//		score += 100
//	} else if countPiece == 3 && countEmpty == 1 {
//		score += 5
//	} else if countPiece == 2 && countEmpty == 2 {
//		score += 2
//	}
//
//	if countOppPiece == 3 && countEmpty == 1 {
//		score -= 4
//	}
//
//	return score
//}
//
//func winningMove(board [][]int, piece int) bool {
//	for y, col := range board {
//		for x, value := range col {
//			if piece != value {
//				continue
//			}
//
//			if checkDiscsOnARow(&Coordinate{x, y}, value) >= 4 {
//				return true
//			}
//		}
//	}
//
//	return false
//}
//
//func checkCenter(board [][]int, piece int) int {
//	score := 0
//	rowCount := len(board)
//
//	centreCol := rowCount / 2
//	centreArray := make([]int, rowCount)
//
//	for r := 0; r < rowCount; r++ {
//		centreArray[r] = board[r][centreCol]
//	}
//
//	for _, nb := range centreArray {
//		if nb == piece {
//			score++
//		}
//	}
//
//	return score * 3
//}
//
//func checkHorizontal(board [][]int, piece int) int {
//	score := 0
//	yCount := len(board)
//	xCount := len(board[0])
//	windowLength := 4
//
//	for y := 0; y < yCount; y++ {
//		yArray := make([]int, xCount)
//		copy(yArray, board[y])
//
//		for x := 0; x < xCount-3; x++ {
//			window := yArray[y : y+windowLength]
//			score += evaluateWindow(window, piece)
//		}
//	}
//
//	return score
//}
//
//func checkVertical(board [][]int, piece int) int {
//	score := 0
//	yCount := len(board)
//	xCount := len(board[0])
//	windowLength := 4
//
//	for x := 0; x < xCount; x++ {
//		xArray := make([]int, xCount)
//		for y := 0; y < yCount; y++ {
//			xArray[y] = board[y][x]
//		}
//
//		for y := 0; y < yCount-3; y++ {
//			window := xArray[y : y+windowLength]
//			score += evaluateWindow(window, piece)
//		}
//	}
//
//	return score
//}
//
//func checkPositiveDiagonal(board [][]int, piece int) int {
//	score := 0
//	yCount := len(board)
//	xCount := len(board[0])
//	windowLength := 4
//
//	for x := 0; x < xCount-3; x++ {
//		for y := 0; y < yCount-3; y++ {
//			window := make([]int, windowLength)
//			for i := 0; i < windowLength; i++ {
//				window[i] = board[y+i][x+i]
//			}
//			score += evaluateWindow(window, piece)
//		}
//	}
//
//	return score
//}
//
//func checkNegativeDiagonal(board [][]int, piece int) int {
//	score := 0
//	yCount := len(board)
//	xCount := len(board[0])
//	windowLength := 4
//
//	for x := 0; x < xCount-3; x++ {
//		for y := 0; y < yCount-3; y++ {
//			window := make([]int, windowLength)
//			for i := 0; i < windowLength; i++ {
//				window[i] = board[y+3-i][x+i]
//			}
//			score += evaluateWindow(window, piece)
//		}
//	}
//
//	return score
//}
