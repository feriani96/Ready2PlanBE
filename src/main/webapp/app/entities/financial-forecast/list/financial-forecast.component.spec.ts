import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FinancialForecastService } from '../service/financial-forecast.service';

import { FinancialForecastComponent } from './financial-forecast.component';

describe('FinancialForecast Management Component', () => {
  let comp: FinancialForecastComponent;
  let fixture: ComponentFixture<FinancialForecastComponent>;
  let service: FinancialForecastService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'financial-forecast', component: FinancialForecastComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [FinancialForecastComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(FinancialForecastComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FinancialForecastComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FinancialForecastService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.financialForecasts?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });

  describe('trackId', () => {
    it('Should forward to financialForecastService', () => {
      const entity = { id: 'ABC' };
      jest.spyOn(service, 'getFinancialForecastIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFinancialForecastIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
