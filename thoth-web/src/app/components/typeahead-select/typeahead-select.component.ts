import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewContainerRef} from '@angular/core';
import {IonInput, IonItem} from "@ionic/angular";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";

@Component({
  selector: 'app-typeahead-select',
  templateUrl: './typeahead-select.component.html',
  styleUrls: ['./typeahead-select.component.scss'],
})
export class TypeaheadSelectComponent implements OnInit {
  items: any[] = [];
  selectedItems: any[] = [];
  @Input() multiple = false;
  title = 'Select Items';
  @Input() value: any = null;
  @Input() fetcher: DataFetcher<any> | undefined;

  @Output() confirm = new EventEmitter<any | undefined>();

  private page = 0;
  loading = true;


  constructor(private parent: IonItem) {
    parent.button = true;
  }

  ngOnInit() {
    this.fetcher?.findAll(this.page).then(page => {
      this.items = page.content;
      if (this.multiple) {
        this.selectedItems = this.value || [];
      }
      this.loading = false;
    })
  }

  trackItems(index: number, item: any) {
    return item.value;
  }


  isChecked(value: string) {
    return this.selectedItems.find((item) => item === value);
  }

  checkboxChange(ev: any) {
    const {checked, value} = ev.detail;

    if (checked) {
      this.selectedItems = [...this.selectedItems, value];
    } else {
      this.selectedItems = this.selectedItems.filter((item) => item !== value);
    }
  }

  onIonInfinite($event: any) {
    this.page++;
    this.fetcher?.findAll(this.page).then(page => {
      this.items = this.items.concat(page.content);
      $event.target.complete();
    })
  }

  confirmChanges() {
    if (this.multiple) {
      this.confirm.emit(this.selectedItems);
    } else {
      this.confirm.emit(this.selectedItems[0]);
    }
  }
}
