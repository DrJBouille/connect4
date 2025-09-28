import {Component, Input} from '@angular/core';
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-simple-value-information',
    imports: [
        AsyncPipe
    ],
  templateUrl: './simple-value-information.html',
  styleUrl: './simple-value-information.css',
})
export class SimpleValueInformation {
  @Input() title!: string;
  @Input() value!: string ;
}
