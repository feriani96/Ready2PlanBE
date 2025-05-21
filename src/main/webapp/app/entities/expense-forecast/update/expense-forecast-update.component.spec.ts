import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExpenseForecastFormService } from './expense-forecast-form.service';
import { ExpenseForecastService } from '../service/expense-forecast.service';
import { IExpenseForecast } from '../expense-forecast.model';
import { IFinancialForecast } from 'app/entities/financial-forecast/financial-forecast.model';
import { FinancialForecastService } from 'app/entities/financial-forecast/service/financial-forecast.service';

import { ExpenseForecastUpdateComponent } from './expense-forecast-update.component';

describe('ExpenseForecast Management Update Component', () => {
  let comp: ExpenseForecastUpdateComponent;
  let fixture: ComponentFixture<ExpenseForecastUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let expenseForecastFormService: ExpenseForecastFormService;
  let expenseForecastService: ExpenseForecastService;
  let financialForecastService: FinancialForecastService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExpenseForecastUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ExpenseForecastUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExpenseForecastUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    expenseForecastFormService = TestBed.inject(ExpenseForecastFormService);
    expenseForecastService = TestBed.inject(ExpenseForecastService);
    financialForecastService = TestBed.inject(FinancialForecastService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call FinancialForecast query and add missing value', () => {
      const expenseForecast: IExpenseForecast = { id: 'CBA' };
      const forecast: IFinancialForecast = { id: '93984e22-41d0-4392-acc5-91bb8f56e1b7' };
      expenseForecast.forecast = forecast;

      const financialForecastCollection: IFinancialForecast[] = [{ id: 'e14a7d59-ee89-4672-9a95-bb995e8ea54d' }];
      jest.spyOn(financialForecastService, 'query').mockReturnValue(of(new HttpResponse({ body: financialForecastCollection })));
      const additionalFinancialForecasts = [forecast];
      const expectedCollection: IFinancialForecast[] = [...additionalFinancialForecasts, ...financialForecastCollection];
      jest.spyOn(financialForecastService, 'addFinancialForecastToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ expenseForecast });
      comp.ngOnInit();

      expect(financialForecastService.query).toHaveBeenCalled();
      expect(financialForecastService.addFinancialForecastToCollectionIfMissing).toHaveBeenCalledWith(
        financialForecastCollection,
        ...additionalFinancialForecasts.map(expect.objectContaining)
      );
      expect(comp.financialForecastsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const expenseForecast: IExpenseForecast = { id: 'CBA' };
      const forecast: IFinancialForecast = { id: 'b5ef333b-5faf-43bc-8b1c-a69baacf449c' };
      expenseForecast.forecast = forecast;

      activatedRoute.data = of({ expenseForecast });
      comp.ngOnInit();

      expect(comp.financialForecastsSharedCollection).toContain(forecast);
      expect(comp.expenseForecast).toEqual(expenseForecast);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpenseForecast>>();
      const expenseForecast = { id: 'ABC' };
      jest.spyOn(expenseForecastFormService, 'getExpenseForecast').mockReturnValue(expenseForecast);
      jest.spyOn(expenseForecastService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expenseForecast });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expenseForecast }));
      saveSubject.complete();

      // THEN
      expect(expenseForecastFormService.getExpenseForecast).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(expenseForecastService.update).toHaveBeenCalledWith(expect.objectContaining(expenseForecast));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpenseForecast>>();
      const expenseForecast = { id: 'ABC' };
      jest.spyOn(expenseForecastFormService, 'getExpenseForecast').mockReturnValue({ id: null });
      jest.spyOn(expenseForecastService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expenseForecast: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expenseForecast }));
      saveSubject.complete();

      // THEN
      expect(expenseForecastFormService.getExpenseForecast).toHaveBeenCalled();
      expect(expenseForecastService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpenseForecast>>();
      const expenseForecast = { id: 'ABC' };
      jest.spyOn(expenseForecastService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expenseForecast });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(expenseForecastService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFinancialForecast', () => {
      it('Should forward to financialForecastService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(financialForecastService, 'compareFinancialForecast');
        comp.compareFinancialForecast(entity, entity2);
        expect(financialForecastService.compareFinancialForecast).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
