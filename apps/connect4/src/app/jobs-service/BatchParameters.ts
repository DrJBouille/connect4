import {JobParameter} from "./JobParameter";

export class BatchParameters {
  constructor(
    public nbOfProcess: number,
    public jobParameter: JobParameter
  ) {}
}
