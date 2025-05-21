import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EntrepriseDetailComponent } from './entreprise-detail.component';

describe('Entreprise Management Detail Component', () => {
  let comp: EntrepriseDetailComponent;
  let fixture: ComponentFixture<EntrepriseDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EntrepriseDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ entreprise: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(EntrepriseDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EntrepriseDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load entreprise on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.entreprise).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
