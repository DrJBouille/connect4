import {Component, ElementRef, Input, OnChanges, ViewChild} from '@angular/core';
import Plotly, {Layout, PieData} from 'plotly.js-dist-min';

@Component({
  selector: 'app-plotly-pie-charts',
  imports: [],
  templateUrl: './plotly-pie-charts.html',
  styleUrl: './plotly-pie-charts.css',
})
export class PlotlyPieCharts implements OnChanges {
  @Input() values!: number[];
  @Input() labels!: string[];
  @Input() colors: string[] = [];

  @ViewChild('chart', {static: true}) chart!: ElementRef;

  ngOnChanges() {
    this.render();
  }

  render() {
    const data: Partial<PieData>[] = [{
      values: this.values,
      labels: this.labels,
      type: 'pie',
      hole: 0.5,
      marker: {
        colors: this.colors
      },
      textinfo: 'label+percent',
      textposition: 'inside',

    }];

    const layout: Partial<Layout> = {
      margin: { t: 0, b: 0, l: 0, r: 0 },
      showlegend: true,
      paper_bgcolor: 'rgba(0,0,0,0)',
      plot_bgcolor: 'rgba(0,0,0,0)',
      legend: {
        font: {
          family: 'Arial, sans-serif',
          color: '#ffffff',
          size: 12
        }
      }
    };

    Plotly.newPlot(this.chart.nativeElement, data, layout, { responsive: true });
  }
}
