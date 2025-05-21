import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('BusinessPlan e2e test', () => {
  const businessPlanPageUrl = '/business-plan';
  const businessPlanPageUrlPattern = new RegExp('/business-plan(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const businessPlanSample = {};

  let businessPlan;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/business-plans+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/business-plans').as('postEntityRequest');
    cy.intercept('DELETE', '/api/business-plans/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (businessPlan) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/business-plans/${businessPlan.id}`,
      }).then(() => {
        businessPlan = undefined;
      });
    }
  });

  it('BusinessPlans menu should load BusinessPlans page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('business-plan');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BusinessPlan').should('exist');
    cy.url().should('match', businessPlanPageUrlPattern);
  });

  describe('BusinessPlan page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(businessPlanPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BusinessPlan page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/business-plan/new$'));
        cy.getEntityCreateUpdateHeading('BusinessPlan');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPlanPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/business-plans',
          body: businessPlanSample,
        }).then(({ body }) => {
          businessPlan = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/business-plans+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [businessPlan],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(businessPlanPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BusinessPlan page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('businessPlan');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPlanPageUrlPattern);
      });

      it('edit button click should load edit BusinessPlan page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BusinessPlan');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPlanPageUrlPattern);
      });

      it('edit button click should load edit BusinessPlan page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BusinessPlan');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPlanPageUrlPattern);
      });

      it('last delete button click should delete instance of BusinessPlan', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('businessPlan').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessPlanPageUrlPattern);

        businessPlan = undefined;
      });
    });
  });

  describe('new BusinessPlan page', () => {
    beforeEach(() => {
      cy.visit(`${businessPlanPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BusinessPlan');
    });

    it('should create an instance of BusinessPlan', () => {
      cy.get(`[data-cy="name"]`).type('Wooden lime sexy').should('have.value', 'Wooden lime sexy');

      cy.get(`[data-cy="description"]`).type('Right-sized red optical').should('have.value', 'Right-sized red optical');

      cy.get(`[data-cy="creationDate"]`).type('64780').should('have.value', '64780');

      cy.get(`[data-cy="entropreneurName"]`).type('Account').should('have.value', 'Account');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        businessPlan = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', businessPlanPageUrlPattern);
    });
  });
});
