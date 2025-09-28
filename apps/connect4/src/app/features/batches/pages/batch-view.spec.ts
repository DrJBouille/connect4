import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BatchView } from './batch-view';

describe('BatchView', () => {
  let component: BatchView;
  let fixture: ComponentFixture<BatchView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BatchView],
    }).compileComponents();

    fixture = TestBed.createComponent(BatchView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
