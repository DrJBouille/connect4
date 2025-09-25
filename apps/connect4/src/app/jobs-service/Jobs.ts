import {JobParameter} from "./JobParameter";
import {Stats} from "./Stats";

export class Jobs {
  constructor(
    public jobsId: string,
    public batchId: string,
    public jobParameter: JobParameter,
    public stats: Stats,
    public status: string
  ) {
  }
}
