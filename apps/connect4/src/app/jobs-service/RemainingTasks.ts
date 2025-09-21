export class RemainingTasks {
  constructor(
   public threadInUse: number,
   public totalThread: number,
   public remainingBatches: number,
   public remainingJobs: number,
   public notStartedJobs: number,
   public pendingJobs: number,
   public runningJobs: number,
   public finishedJobs: number,
   public failedJobs: number,
  ) {
  }
}
