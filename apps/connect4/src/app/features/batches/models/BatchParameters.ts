export class BatchParameters {
  constructor(
    public nbOfProcess: number,
    public jobParameter: JobParameters
  ) {}
}

export class JobParameters {
  constructor(
    public redDeepness: number,
    public yellowDeepness: number
  ) {}
}

