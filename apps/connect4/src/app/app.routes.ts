import { Route } from '@angular/router';
import {JobsView} from "./features/jobs/pages/jobs-view";
import {BatchView} from "./features/batches/pages/batch-view";

export const appRoutes: Route[] = [
  { path: '', redirectTo: 'batches', pathMatch: 'full' },
  {path: 'batches', component: BatchView},
  {path: 'batches/:batchId', component: JobsView},
  { path: '**', redirectTo: 'batches' }
];
