import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SimpleValueInformation } from './simple-value-information';

describe('SimpleValueInformation', () => {
  let component: SimpleValueInformation;
  let fixture: ComponentFixture<SimpleValueInformation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SimpleValueInformation],
    }).compileComponents();

    fixture = TestBed.createComponent(SimpleValueInformation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
