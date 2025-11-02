import {Status} from "../../../shared/models/Status";

export class Jobs {
  constructor(
    public id: string,
    public status: Status
  ) {
  }
}
