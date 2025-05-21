import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EntrepriseFormService } from './entreprise-form.service';
import { EntrepriseService } from '../service/entreprise.service';
import { IEntreprise } from '../entreprise.model';

import { EntrepriseUpdateComponent } from './entreprise-update.component';

describe('Entreprise Management Update Component', () => {
  let comp: EntrepriseUpdateComponent;
  let fixture: ComponentFixture<EntrepriseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let entrepriseFormService: EntrepriseFormService;
  let entrepriseService: EntrepriseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EntrepriseUpdateComponent],
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
      .overrideTemplate(EntrepriseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EntrepriseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    entrepriseFormService = TestBed.inject(EntrepriseFormService);
    entrepriseService = TestBed.inject(EntrepriseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const entreprise: IEntreprise = { id: 'CBA' };

      activatedRoute.data = of({ entreprise });
      comp.ngOnInit();

      expect(comp.entreprise).toEqual(entreprise);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEntreprise>>();
      const entreprise = { id: 'ABC' };
      jest.spyOn(entrepriseFormService, 'getEntreprise').mockReturnValue(entreprise);
      jest.spyOn(entrepriseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ entreprise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: entreprise }));
      saveSubject.complete();

      // THEN
      expect(entrepriseFormService.getEntreprise).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(entrepriseService.update).toHaveBeenCalledWith(expect.objectContaining(entreprise));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEntreprise>>();
      const entreprise = { id: 'ABC' };
      jest.spyOn(entrepriseFormService, 'getEntreprise').mockReturnValue({ id: null });
      jest.spyOn(entrepriseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ entreprise: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: entreprise }));
      saveSubject.complete();

      // THEN
      expect(entrepriseFormService.getEntreprise).toHaveBeenCalled();
      expect(entrepriseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEntreprise>>();
      const entreprise = { id: 'ABC' };
      jest.spyOn(entrepriseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ entreprise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(entrepriseService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
