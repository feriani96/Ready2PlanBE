import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RevenueForecastDetailComponent } from './revenue-forecast-detail.component';

describe('RevenueForecast Management Detail Component', () => {
  let comp: RevenueForecastDetailComponent;
  let fixture: ComponentFixture<RevenueForecastDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RevenueForecastDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ revenueForecast: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(RevenueForecastDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RevenueForecastDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load revenueForecast on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.revenueForecast).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
