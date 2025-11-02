import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobsView } from './job-view.component';

describe('BatchView', () => {
  let component: JobsView;
  let fixture: ComponentFixture<JobsView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobsView],
    }).compileComponents();

    fixture = TestBed.createComponent(JobsView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
