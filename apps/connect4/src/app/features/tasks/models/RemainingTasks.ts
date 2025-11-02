export class RemainingTasks {
  constructor(
   public notStartedJobs: number,
   public pendingJobs: number,
   public runningJobs: number,
   public finishedJobs: number,
   public failedJobs: number,
  ) {
  }
}
