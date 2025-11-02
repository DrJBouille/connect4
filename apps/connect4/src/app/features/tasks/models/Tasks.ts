import {Stats} from "./Stats";
import {Status} from "../../../shared/models/Status";

export class Tasks {
  constructor(
    public id: string,
    public jobId: string,
    public stats: Stats | null,
    public status: Status
  ) {
  }
}
