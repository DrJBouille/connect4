import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-simple-value-information',
    imports: [],
  templateUrl: './simple-value-information.html',
  styleUrl: './simple-value-information.css',
})
export class SimpleValueInformation {
  @Input() title!: string;
  @Input() value!: string ;
}
