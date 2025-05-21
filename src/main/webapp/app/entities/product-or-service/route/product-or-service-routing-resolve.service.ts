import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductOrService } from '../product-or-service.model';
import { ProductOrServiceService } from '../service/product-or-service.service';

@Injectable({ providedIn: 'root' })
export class ProductOrServiceRoutingResolveService implements Resolve<IProductOrService | null> {
  constructor(protected service: ProductOrServiceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductOrService | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productOrService: HttpResponse<IProductOrService>) => {
          if (productOrService.body) {
            return of(productOrService.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
