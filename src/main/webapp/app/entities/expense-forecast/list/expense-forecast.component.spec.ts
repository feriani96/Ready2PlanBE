import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ExpenseForecastService } from '../service/expense-forecast.service';

import { ExpenseForecastComponent } from './expense-forecast.component';

describe('ExpenseForecast Management Component', () => {
  let comp: ExpenseForecastComponent;
  let fixture: ComponentFixture<ExpenseForecastComponent>;
  let service: ExpenseForecastService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'expense-forecast', component: ExpenseForecastComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ExpenseForecastComponent],
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
      .overrideTemplate(ExpenseForecastComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExpenseForecastComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ExpenseForecastService);

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
    expect(comp.expenseForecasts?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });

  describe('trackId', () => {
    it('Should forward to expenseForecastService', () => {
      const entity = { id: 'ABC' };
      jest.spyOn(service, 'getExpenseForecastIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getExpenseForecastIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
