import {Component, EventEmitter, Input, output, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatFormField} from "@angular/material/form-field";
import {MatInput, MatLabel} from "@angular/material/input";
import {JobParameters} from "../../models/BatchParameters";
import {Images} from "../../models/Images";
import {MatSelect, MatOption} from "@angular/material/select";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-card-parameters',
  imports: [
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatSelect,
    MatOption,
    MatOption,
    MatIcon
  ],
  templateUrl: './card-parameters.html',
  styleUrl: './card-parameters.css',
})
export class CardParameters {
  @Input() parameters!: JobParameters;
  @Output() remove = new EventEmitter();

  imagesValue = Object.values(Images)
}
