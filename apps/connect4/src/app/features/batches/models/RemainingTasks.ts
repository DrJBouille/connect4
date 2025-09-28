export class RemainingTasks {
  constructor(
   public threadInUse: number,
   public totalThread: number,
   public remainingBatches: number,
   public remainingJobs: number,
   public notStartedJobs: number,
   public runningJobs: number,
   public finishedJobs: number,
  ) {}
}
