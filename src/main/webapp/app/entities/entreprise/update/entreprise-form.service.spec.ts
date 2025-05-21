import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../entreprise.test-samples';

import { EntrepriseFormService } from './entreprise-form.service';

describe('Entreprise Form Service', () => {
  let service: EntrepriseFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EntrepriseFormService);
  });

  describe('Service methods', () => {
    describe('createEntrepriseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEntrepriseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom_etp: expect.any(Object),
            pays: expect.any(Object),
            telephone: expect.any(Object),
            description: expect.any(Object),
            deviseSize: expect.any(Object),
            devise: expect.any(Object),
          })
        );
      });

      it('passing IEntreprise should create a new form with FormGroup', () => {
        const formGroup = service.createEntrepriseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom_etp: expect.any(Object),
            pays: expect.any(Object),
            telephone: expect.any(Object),
            description: expect.any(Object),
            deviseSize: expect.any(Object),
            devise: expect.any(Object),
          })
        );
      });
    });

    describe('getEntreprise', () => {
      it('should return NewEntreprise for default Entreprise initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEntrepriseFormGroup(sampleWithNewData);

        const entreprise = service.getEntreprise(formGroup) as any;

        expect(entreprise).toMatchObject(sampleWithNewData);
      });

      it('should return NewEntreprise for empty Entreprise initial value', () => {
        const formGroup = service.createEntrepriseFormGroup();

        const entreprise = service.getEntreprise(formGroup) as any;

        expect(entreprise).toMatchObject({});
      });

      it('should return IEntreprise', () => {
        const formGroup = service.createEntrepriseFormGroup(sampleWithRequiredData);

        const entreprise = service.getEntreprise(formGroup) as any;

        expect(entreprise).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEntreprise should not enable id FormControl', () => {
        const formGroup = service.createEntrepriseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEntreprise should disable id FormControl', () => {
        const formGroup = service.createEntrepriseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
