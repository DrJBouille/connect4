export class Stats {
  constructor(
    public gameTime: number,
    public doesRedWin: boolean | null,
    public redDeepness: number,
    public yellowDeepness: number,
    public moves: Move[]
  ) {
  }
}

export class Move {
  constructor(
    public coordinate: Coordinate,
    public isRedTurn: Boolean,
    public time: number
  ) {
  }
}

export class Coordinate {
  constructor(
    public x: number,
    public y: number
  ) {
  }
}
