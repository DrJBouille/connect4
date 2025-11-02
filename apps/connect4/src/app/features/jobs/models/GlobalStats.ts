export class GlobalStats {
  constructor(
    public averageGameTime: number,
    public minGameTime: number,
    public maxGameTime: number,
    public averageMoves: number,
    public minMoves: number,
    public maxMoves: number,
    public redWins: number,
    public yellowWins: number,
    public draws: number,
  ) {
  }
}
