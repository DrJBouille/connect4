import {Images} from "./Images";

export class BatchParameters {
  constructor(
    public jobParameters: JobParameters[]
  ) {}
}

export class JobParameters {
  constructor(
    public nbOfProcess: number,
    public redDeepness: number,
    public yellowDeepness: number,
    public image: Images
  ) {}
}

