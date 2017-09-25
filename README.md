# lrecyclerview
pull to refresh and load more recyclerview

your header or footer or empty doing this

	public class SimpleFooter extends FooterWrapper {

    		public SimpleFooter(Context context) {
        	super(context);
   		}
		....
	}
	
your activity write as this

	public class LRecyclerViewActivity extends Activity{
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LrecyclerView recycler = (LRecyclerView) findViewById(R.id.recycler);
		GridLayoutManager manager = new GridLayoutManager(this,2);
		recycler.setLayoutManager(manager);
		SparseIntArray layouts = new SparseIntArray();
		layouts.put(1,R.layout.img_item);
		layouts.put(2,R.layout.text_item);
		recycler.setAdapter(new MultiTypeAdapter<String>().list(list)
			.generator(new LAdapter.TypeGenerator<MultiTypeAdapter<String>>() {\n
				@Override
				public int generate(MultiTypeAdapter<String> adapter, int position) {
					return position % 3 == 0 ? position % 2 == 0 ? 1 : 2 : 1;
					}
					}).layouts(layouts).binder(new LAdapter.TypeBinder<MultiTypeAdapter<String>>() {
					@Override            
					public void bind(MultiTypeAdapter<String> adapter, LHolder holder, int position, int viewType) { 
						if (viewType == 1){
							holder.setImage(R.id.img,R.mipmap.ic_launcher);
						}else {
							holder.setText(R.id.text,position + "");
						}
					}
				}));
		recycler.setRefreshEnable(true);
		recycler.setHeader(new SimpleHeader(this));
		recycler.addRefreshListener(this);
		recycler.setLoadEnable(true);
		SimpleFooter simpleFooter = new SimpleFooter(this);
		recycler.setFooter(simpleFooter);
		recycler.setMode(LRecyclerView.LOAD_MODE_DEFAULT);
		recycler.setComputeBottom(3);
		SimpleEmpty simpleEmpty = new SimpleEmpty(this);
		recycler.setEmpty(simpleEmpty);
		}
	}
# Download

	dependencies {
		compile 'leicher:lrecyclerview:1.0.1'
	}
