import {JobParameter} from "./JobParameter";
import {Stats} from "./Stats";

export class Jobs {
  constructor(
    public id: string,
    public jobParameter: JobParameter,
    public stats: Stats,
    public containerStatus: string
  ) {
  }
}
