import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PlotlyLineCharts } from './plotly-line-charts';

describe('PlotlyLineCharts', () => {
  let component: PlotlyLineCharts;
  let fixture: ComponentFixture<PlotlyLineCharts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlotlyLineCharts],
    }).compileComponents();

    fixture = TestBed.createComponent(PlotlyLineCharts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
