jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ExpenseForecastService } from '../service/expense-forecast.service';

import { ExpenseForecastDeleteDialogComponent } from './expense-forecast-delete-dialog.component';

describe('ExpenseForecast Management Delete Component', () => {
  let comp: ExpenseForecastDeleteDialogComponent;
  let fixture: ComponentFixture<ExpenseForecastDeleteDialogComponent>;
  let service: ExpenseForecastService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ExpenseForecastDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(ExpenseForecastDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ExpenseForecastDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ExpenseForecastService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete('ABC');
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith('ABC');
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
