import {Stats} from "./Stats";
import {Status} from "../../../shared/models/Status";

export class Jobs {
  constructor(
    public jobsId: string,
    public batchId: string,
    public stats: Stats | null,
    public status: Status
  ) {
  }
}
