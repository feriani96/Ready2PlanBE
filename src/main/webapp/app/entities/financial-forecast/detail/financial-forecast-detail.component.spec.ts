import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FinancialForecastDetailComponent } from './financial-forecast-detail.component';

describe('FinancialForecast Management Detail Component', () => {
  let comp: FinancialForecastDetailComponent;
  let fixture: ComponentFixture<FinancialForecastDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FinancialForecastDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ financialForecast: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(FinancialForecastDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FinancialForecastDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load financialForecast on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.financialForecast).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
