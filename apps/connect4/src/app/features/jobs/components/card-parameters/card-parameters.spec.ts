import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CardParameters } from './card-parameters';

describe('CardParameters', () => {
  let component: CardParameters;
  let fixture: ComponentFixture<CardParameters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardParameters],
    }).compileComponents();

    fixture = TestBed.createComponent(CardParameters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
