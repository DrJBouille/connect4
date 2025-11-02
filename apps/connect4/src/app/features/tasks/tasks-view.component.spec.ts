import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TasksView } from './tasks-view.component';

describe('JobsView', () => {
  let component: TasksView;
  let fixture: ComponentFixture<TasksView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TasksView],
    }).compileComponents();

    fixture = TestBed.createComponent(TasksView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
