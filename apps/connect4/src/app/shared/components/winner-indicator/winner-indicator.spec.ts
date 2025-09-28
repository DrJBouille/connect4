import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WinnerIndicator } from './winner-indicator';

describe('WinnerIndicator', () => {
  let component: WinnerIndicator;
  let fixture: ComponentFixture<WinnerIndicator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WinnerIndicator],
    }).compileComponents();

    fixture = TestBed.createComponent(WinnerIndicator);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
