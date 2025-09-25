import {Coordinate} from "./Coordinate";

export class Move {
  constructor(
    public board: Boolean | null[][],
    public coordinate: Coordinate,
    public time: number
  ) {
  }
}
