import {Stats} from "./Stats";
import {JobParameters} from "../../batches/models/BatchParameters";
import {Status} from "../../../shared/models/Status";

export class Jobs {
  constructor(
    public jobsId: string,
    public batchId: string,
    public jobParameter: JobParameters,
    public stats: Stats | null,
    public status: Status
  ) {
  }
}
