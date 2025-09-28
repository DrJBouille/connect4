import {Component, ElementRef, Input, OnChanges, ViewChild} from '@angular/core';
import Plotly, {Data, Layout} from "plotly.js-dist-min";

@Component({
  selector: 'app-plotly-line-charts',
  imports: [],
  templateUrl: './plotly-line-charts.html',
  styleUrl: './plotly-line-charts.css',
})
export class PlotlyLineCharts implements OnChanges {
  @Input() x!: number[];
  @Input() y!: number[];
  @Input() title!: string;

  @ViewChild('chart', {static: true}) chart!: ElementRef;

  ngOnChanges() {
    this.render();
  }

  render() {
    const data: Partial<Data>[] = [{
      x: this.x,
      y: this.y,
      type: 'scatter',
      mode: 'lines',
      line: { color: '#e5e7eb', width: 3 },
      name: this.title
    }];

    const layout: Partial<Layout> = {
      title: {
        text: this.title,
        font: { color: '#e5e7eb' }
      },
      margin: { t: 40, b: 0, l: 30, r: 0 },
      showlegend: true,
      paper_bgcolor: 'rgba(0,0,0,0)',
      plot_bgcolor: 'rgba(0,0,0,0)',
      font: { color: '#e5e7eb' },
      xaxis: { showgrid: false, zeroline: false },
      yaxis: { showgrid: false, zeroline: false }
    };

    Plotly.newPlot(this.chart.nativeElement, data, layout, { responsive: true });
  }
}
