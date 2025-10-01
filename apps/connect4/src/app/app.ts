import {Component} from '@angular/core';
import {RouterLink, RouterOutlet} from "@angular/router";
import {MatIcon} from "@angular/material/icon";

@Component({
  imports: [RouterOutlet, MatIcon, RouterLink],
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
