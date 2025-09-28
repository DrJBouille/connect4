import {Status} from "../../../shared/models/Status";

export class Batch {
  constructor(
    public id: string,
    public status: Status
  ) {
  }
}
