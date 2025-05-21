import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductOrService } from '../product-or-service.model';

@Component({
  selector: 'jhi-product-or-service-detail',
  templateUrl: './product-or-service-detail.component.html',
})
export class ProductOrServiceDetailComponent implements OnInit {
  productOrService: IProductOrService | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productOrService }) => {
      this.productOrService = productOrService;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
