var ssconf = {
		// 数据源
		ssDataSource : {
			type : "com.alibaba.druid.pool.DruidDataSource",
			events : {
				depose : "close"
			},
			fields : {
				driverClassName : {
					java : "$conf.get('ss-db-driver')"
				},
				url : {
					java : "$conf.get('ss-db-url')"
				},
				username : {
					java : "$conf.get('ss-db-username')"
				},
				password : {
					java : "$conf.get('ss-db-password')"
				},
				initialSize : 10,
				maxActive : 100,
				testOnReturn : true,
				validationQuery : "select 1",
				filters : "stat"
			}
		},
		// Dao
		ssDao : {
			type : 'org.nutz.dao.impl.NutDao',
			args : [ {
				refer : "ssDataSource"
			}, {
				type : 'org.nutz.dao.impl.FileSqlManager',
				args : [ 'sql' ]
			} ]
		}
}