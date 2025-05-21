import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RevenueForecastService } from '../service/revenue-forecast.service';

import { RevenueForecastComponent } from './revenue-forecast.component';

describe('RevenueForecast Management Component', () => {
  let comp: RevenueForecastComponent;
  let fixture: ComponentFixture<RevenueForecastComponent>;
  let service: RevenueForecastService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'revenue-forecast', component: RevenueForecastComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [RevenueForecastComponent],
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
      .overrideTemplate(RevenueForecastComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RevenueForecastComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RevenueForecastService);

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
    expect(comp.revenueForecasts?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });

  describe('trackId', () => {
    it('Should forward to revenueForecastService', () => {
      const entity = { id: 'ABC' };
      jest.spyOn(service, 'getRevenueForecastIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getRevenueForecastIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
