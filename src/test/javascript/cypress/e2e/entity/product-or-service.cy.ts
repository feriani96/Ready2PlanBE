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

describe('ProductOrService e2e test', () => {
  const productOrServicePageUrl = '/product-or-service';
  const productOrServicePageUrlPattern = new RegExp('/product-or-service(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const productOrServiceSample = {};

  let productOrService;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/product-or-services+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/product-or-services').as('postEntityRequest');
    cy.intercept('DELETE', '/api/product-or-services/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (productOrService) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/product-or-services/${productOrService.id}`,
      }).then(() => {
        productOrService = undefined;
      });
    }
  });

  it('ProductOrServices menu should load ProductOrServices page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('product-or-service');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProductOrService').should('exist');
    cy.url().should('match', productOrServicePageUrlPattern);
  });

  describe('ProductOrService page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(productOrServicePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ProductOrService page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/product-or-service/new$'));
        cy.getEntityCreateUpdateHeading('ProductOrService');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productOrServicePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/product-or-services',
          body: productOrServiceSample,
        }).then(({ body }) => {
          productOrService = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/product-or-services+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [productOrService],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(productOrServicePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ProductOrService page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('productOrService');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productOrServicePageUrlPattern);
      });

      it('edit button click should load edit ProductOrService page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductOrService');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productOrServicePageUrlPattern);
      });

      it('edit button click should load edit ProductOrService page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ProductOrService');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productOrServicePageUrlPattern);
      });

      it('last delete button click should delete instance of ProductOrService', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('productOrService').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', productOrServicePageUrlPattern);

        productOrService = undefined;
      });
    });
  });

  describe('new ProductOrService page', () => {
    beforeEach(() => {
      cy.visit(`${productOrServicePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ProductOrService');
    });

    it('should create an instance of ProductOrService', () => {
      cy.get(`[data-cy="name"]`).type('firmware').should('have.value', 'firmware');

      cy.get(`[data-cy="description"]`).type('Mouse Executive').should('have.value', 'Mouse Executive');

      cy.get(`[data-cy="unitPrice"]`).type('89789').should('have.value', '89789');

      cy.get(`[data-cy="estimatedMonthlySales"]`).type('36815').should('have.value', '36815');

      cy.get(`[data-cy="durationInMonths"]`).type('82193').should('have.value', '82193');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        productOrService = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', productOrServicePageUrlPattern);
    });
  });
});
