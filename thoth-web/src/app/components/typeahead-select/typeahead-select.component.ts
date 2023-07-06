import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewContainerRef} from '@angular/core';
import {IonInput, IonItem} from "@ionic/angular";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";
import {SearchComponent} from "../../common/utils/ui-patterns/search-component";

@Component({
  selector: 'app-typeahead-select',
  templateUrl: './typeahead-select.component.html',
  styleUrls: ['./typeahead-select.component.scss'],
})
export class TypeaheadSelectComponent extends SearchComponent implements OnInit {
  items: any[] = [];
  selectedItems: any[] = [];
  @Input() multiple = false;
  title = 'Select Items';
  @Input() value: any = null;
  @Input() fetcher: DataFetcher<any> | undefined;

  @Output() confirm = new EventEmitter<any | undefined>();

  private page = 0;
  loading = true;
  selectedMap: any = {};


  constructor(private parent: IonItem) {
    super();
    parent.button = true;
  }

  ngOnInit() {
    return this.loadPageData();
  }

  async loadPageData() {
    this.page = 0;
    this.loading = true;
    this.items = [];
    this.fetch()
  }


  override composeSearchFilter(): string {
    if (this.search) {
      return 'name==*' + this.search + '*';
    }
    return ''
  }

  fetch() {
    this.fetcher?.findAll(this.page, this.composeSearchFilter()).then(page => {
      this.items = page.content.filter(i => !this.selectedItems.map(e => e.id).includes(i.id));
      if (this.multiple) {
        this.selectedItems = this.value || [];
        for (let selectedItem of this.selectedItems) {
          this.selectedMap[selectedItem.id] = true;
        }
      }
      this.loading = false;
    })
  }

  checkboxChange(ev: any) {
    const {checked, value} = ev.detail;
    if (this.selectedMap[value.id]) {
      this.selectedItems = this.selectedItems.filter((item) => item.id !== value.id);
      this.selectedMap[value.id] = false;
    } else {
      this.selectedItems = [...this.selectedItems, value];
      this.selectedMap[value.id] = true;
    }

  }

  onIonInfinite($event: any) {
    this.page++;
    this.fetcher?.findAll(this.page, this.composeSearchFilter()).then(page => {
      this.items = this.items.concat(page.content.filter(i => !this.selectedItems.map(e => e.id).includes(i.id)));
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


  removeItem(items:any[] , item: any) {
    return items.filter(i => i !== item);
  }
}
