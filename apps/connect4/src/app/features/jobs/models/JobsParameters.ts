import {Images} from "./Images";

export class JobsParameters {
  constructor(
    public parameters: Parameters[]
  ) {}
}

export class Parameters {
  constructor(
    public nbOfProcess: number,
    public redDeepness: number,
    public yellowDeepness: number,
    public image: Images
  ) {}
}

