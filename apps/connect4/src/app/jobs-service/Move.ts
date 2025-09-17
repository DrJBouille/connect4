export class Move {
  constructor(
    public time: number,
    public coordinate: [number, number],
    public board: Boolean | null[][]
  ) {
  }
}
