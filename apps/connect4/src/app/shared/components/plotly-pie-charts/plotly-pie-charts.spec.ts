import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PlotlyPieCharts } from './plotly-pie-charts';

describe('PlotlyPieCharts', () => {
  let component: PlotlyPieCharts;
  let fixture: ComponentFixture<PlotlyPieCharts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlotlyPieCharts],
    }).compileComponents();

    fixture = TestBed.createComponent(PlotlyPieCharts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
