import { Route } from '@angular/router';
import {TasksView} from "./features/tasks/tasks-view.component";
import {JobsView} from "./features/jobs/job-view.component";

export const appRoutes: Route[] = [
  { path: '', redirectTo: 'jobs', pathMatch: 'full' },
  {path: 'jobs', component: JobsView},
  {path: 'jobs/:jobId', component: TasksView},
  { path: '**', redirectTo: 'jobs' }
];
