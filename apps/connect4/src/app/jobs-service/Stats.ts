import {Move} from "./Move";

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
