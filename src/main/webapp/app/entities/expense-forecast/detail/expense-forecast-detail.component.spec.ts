import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ExpenseForecastDetailComponent } from './expense-forecast-detail.component';

describe('ExpenseForecast Management Detail Component', () => {
  let comp: ExpenseForecastDetailComponent;
  let fixture: ComponentFixture<ExpenseForecastDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExpenseForecastDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ expenseForecast: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(ExpenseForecastDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ExpenseForecastDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load expenseForecast on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.expenseForecast).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
