"usr strict";

var CToken = function(Provider, contract){
	//连接URL信息
	this.currentProvider = Provider;
	
	//Token对应的合约地址
	this.contractAccount = contract;

	//web3对象
	this.web3;

	//连接URL成功标志，缺省为false
	this.isConnected = false;

	//访问网络的ID，缺省为0
	this.chainID;
	
	//系统gas价格
	this.gasPrice;

    //Token合约拥有者
    this.contractOwner;

	//Token decimals
	this.tokenDecimals = 8;
};

/**
 * 初始化CToken对象,主要创建web3、网络初始化连接和获取相关网络信息 
 * 
 * @method initCToken 
 * @param {callback(error, result)} 可选，回调返回函数
 * 			连接成功error返回为false，result为“connected success”
 * 			连接失败error返回为true, result为“connected failed”
 * @return 成功返回true，失败返回false。
 * @note   因为Web3js是异步回调机制，当函数返回为true时，并不代表已经成功建立连接。
 * 			此时应该进一步通过回调函数获取详细的连接建立信息。 
 */
CToken.prototype.initCToken = function(callback){
	var _this = this;
	
    //尝试连接以太坊网络，获取web3对象
    if(typeof _this.web3 !== 'undefined') {
        web3 = new Web3(_this.web3.currentProvider);
		return true;
    } else {
		//判断是http还是websocket
		var locate = _this.currentProvider.indexOf(':');
		var mode = _this.currentProvider.slice(0, locate);

		//根据不同的网络模尝试创建web3对象
		if(mode == "ws"){
			web3 = new Web3(new Web3.providers.WebsocketProvider(_this.currentProvider));
		}else if(mode == "http"){
			web3 = new Web3(new Web3.providers.HttpProvider(_this.currentProvider));
		}else{
			console.log("sorry, do not support network mode");
			return false;
		}

		//检查网络连接是否建立
		var pListening = web3.eth.net.isListening();
		Promise.all([pListening]).then(function(result){
			var connected = result[0];

			if(connected){
				//成功连接网络
				console.log("connect ", _this.currentProvider, " success");
				_this.isConnected = true;
				
				//格式化获取Owner数据
				formatedData = web3.eth.abi.encodeFunctionSignature('Owner()');
				rawTx1 = {
					to: _this.contractAccount,
					data: formatedData
				};

				//连接成功，获取网络ID、gasPrice、合约owner和用户注册管理账户信息
				var pGasPrice = web3.eth.getGasPrice();
				var pID = web3.eth.net.getId();
				var pOwner = web3.eth.call(rawTx1);
				Promise.all([pGasPrice, pID, pOwner]).then(function(value){
					var owner;
					_this.gasPrice = value[0];
					_this.chainID = value[1];
					owner = value[2];
					_this.contractOwner = '0x' + owner.slice(26);

					if(typeof callback != 'undefined'){
						callback(true, "network connected success");
					}		
				});			

			}else{
				//无法连接网络
				_this.isConnected = false;
				console.log('can not connect network');
				web3.setProvider(_this.currentProvider);
				if(typeof callback != 'undefined'){
					callback(false, "network connected failed");
				}
			}
		});
	}
	return true;
}

/**
 * 根据钱包名称和地址查找本地是否有钱包
 * 
 * @method findWallet 
 * @param {walletName} 导入钱包名称
 * 		  {accountAddress} 钱包地址信息
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.findWallet = function(walletName, accountAddress){
	var value, wallet;
	 
	if(!web3.utils.isAddress(accountAddress)){
		console.log("invalid account");
		return false;
	}

	//遍历本地localStorage
	for(var key in localStorage){
		value = localStorage.getItem(key);
		wallet = JSON.parse(value);

		//如果钱包为空，则退出
		if(wallet == null)
			continue;

		//进行钱包名称和地址是否匹配检查
		if(key.localeCompare(walletName) == 0){
			var address = '0x' + wallet[0].address;
			if(address.toLowerCase().localeCompare(accountAddress.toLowerCase()) == 0){
				return true;
			}
		}
	}
	return false;
}		


/**
 * 创建钱包，一个钱包仅允许一个账户，并使用口令进行加密保护 
 * 
 * @method createWallet
 * @param {walletName} 钱包名称 {password} 钱包加密保护口令
 * @return 成功返回钱包内部账户地址信息，失败返回null。如果
 * 			钱包已经存在，就直接返回钱包地址
 */
CToken.prototype.createWallet = function(walletName, password){
    var success;
 	var account;
	var seed;
	var time = new Date();
	var currentTime;

	//检查本地是否已经有对应钱包名
	for(var key in localStorage){
		if(key.localeCompare(walletName) == 0){
			var value, wallet;
			console.log(walletName + " already registered");
			value = localStorage.getItem(walletName);
		    wallet = JSON.parse(value);

			//如果钱包已经存在，则直接反馈钱包信息	
			return '0x' + wallet[0].address.toLowerCase();
		}
	}
	
	//产生随机因子
	currentTime = web3.utils.toHex(time.getTime());
	seed = web3.utils.sha3(currentTime);
	seed += web3.utils.sha3(password);
	seed += web3.utils.randomHex(32);
	seed = web3.utils.sha3(seed);
	//创建钱包和账户
	web3.eth.accounts.wallet.clear();
	account = web3.eth.accounts.create(seed);
    
	//把账户添加到钱包中，并使用口令进行保护
    web3.eth.accounts.wallet.add(account);
    success = web3.eth.accounts.wallet.save(password, walletName);

    if(success){
        return account.address.toLowerCase(); //返回账户地址信息
    }else{
        return;
    }
}


/**
 * 根据钱包名称和钱包地址删除钱包 
 * 
 * @method removeWallet
 * @param {walletName} 钱包名称 {password} 钱包加密保护口令 {accountAddress} 钱包地址
 * @return 成功返回true，失败返回false
 */
CToken.prototype.removeWallet = function(walletName, password, accountAddress){
	if(!web3.utils.isAddress(accountAddress)){
		console.log("invalid account");
		return false;
	}
	
	//查找是否有该钱包
	if(!this.findWallet(walletName, accountAddress.toLowerCase())){
		console.log("sorry, "+ walletName + ":" + accountAddress + " does not existed in local");
		return false;
	}

	//通过口令验证钱包
    var privateKey = this.loadWallet(walletName, password, accountAddress);
    if(typeof privateKey == 'undefined'){
        console.log("verify wallet and password failed");
        return false;
    }
	
	//删除钱包信息
	web3.eth.accounts.wallet.remove(accountAddress);	
	//删除localStorage
	localStorage.removeItem(walletName);
	return true;
}


/**
 * 使用口令打开钱包
 * 
 * @method loadWallet
 * @param {walletName} 钱包名称，使用钱包名称区分不同钱包
 * 		  {password} 钱包访问口令
 * 		  {accountAddress optional} 账户地址信息 
 * @return 成功则返回账户私钥，失败则返回null
 */
CToken.prototype.loadWallet = function(walletName,  password, accountAddress){

	if(!web3.utils.isAddress(accountAddress)){
		console.log("invalid account");
		return;
	}

    try{
		//根据钱包名称打开钱包，如果口令不正确则跑出了异常
		var wallet = web3.eth.accounts.wallet.load(password, walletName);
		
		//查找钱包里面是否有对应账户
		for(var i = 0; i < wallet.length; i++){
			if(accountAddress.toLowerCase().localeCompare(wallet[i].address.toLowerCase()) == 0){
				return wallet[i].privateKey;
			}
		}
		//没找到对应account，返回失败
		console.log("no account in wallet");
		return;
    }catch(err){
        console.log("wrong password");
        return;
    }
}

/**
 * 导出wallet keystore信息，导出前需要进行口令认证
 * 
 * @method exportWallet
 * @param {walletName} 钱包名称，使用钱包名称区分不同钱包
 * 		  {password} 钱包访问口令
 * @return 成功则导出钱包keystore信息，失败则返回null
 */
CToken.prototype.exportWallet = function(walletName, password){

    try{
		//根据钱包名称打开钱包，如果口令不正确则抛出了异常
		var wallet = web3.eth.accounts.wallet.load(password, walletName);
	
		//获取并返回钱包keystore数据
		return localStorage.getItem(walletName);
	}catch(err){
        console.log("wrong password");
        return;
    }
}

/**
 * 导入wallet keystore信息
 * 
 * @method importWallet 
 * @param {walletName} 导入钱包名称
 * 		  {keystore} 钱包keystore信息
 * 		  {password} keystore保护口令
 * @return 成功则导出keystore对应的账户地址信息，失败则返回null
 */
CToken.prototype.importWallet = function(walletName, keystore, password){

	//解析keystore为json格式
	var json = JSON.parse(keystore);
	var address = '0x'+json[0].address.toLowerCase();
	
	//遍历本地localStorage
	for(var key in localStorage){
		var value, wallet;

		//检查是否钱包重名
		if(key.localeCompare(walletName) == 0){
			console.log(walletName + " already existed");
			return;
		}

		value = localStorage.getItem(key);
		wallet = JSON.parse(value);
		
		//如果钱包为空，则退出
		if(wallet == null)
			continue;

		//检查keystore是否已经存在
		if(address.toLowerCase().localeCompare('0x' + wallet[0].address.toLowerCase()) == 0){
			console.log(address + " already existed");
			return;
		}
	}

	//导入keystore数据
	localStorage.setItem(walletName, keystore);
	
    try{
		//尝试通过口令验证keystore
		web3.eth.accounts.wallet.load(password, walletName);;
	
		//返回keystore对应的账户地址
		return address;
    }catch(err){
        console.log("wrong password");
		localStorage.removeItem(walletName);
		return;
    }
}

/**
 * 根据钱包名称和地址查找本地是否有钱包
 * 
 * @method findWallet 
 * @param {walletName} 导入钱包名称
 * 		  {accountAddress} 钱包地址信息
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.findWallet = function(walletName, accountAddress){
	var value, wallet;
	
	//遍历本地localStorage
	for(var key in localStorage){
		value = localStorage.getItem(key);
		wallet = JSON.parse(value);

		//如果钱包为空，则退出
		if(wallet == null)
			continue;	

		//进行钱包名称和地址是否匹配检查
		if(key.localeCompare(walletName) == 0){
			var address = '0x' + wallet[0].address;
			if(address.toLowerCase().localeCompare(accountAddress.toLowerCase()) == 0){
				return true;
			}
		}
	}
	return false;
}		

/**
 * 获取本地所有钱包信息
 *
 * @method getWalletList 
 * @param  null 
 * @return 返回钱包信息列表(json格式)，包括{name, address}
 */
CToken.prototype.getWalletList = function(){
	var walletList = new Array();

	//遍历本地localStorage
	for(var key in localStorage){
		var value, wallet, walletInfo;
		value = localStorage.getItem(key);
		wallet = JSON.parse(value);

		//如果为空，则继续
		if(wallet == null)
			continue;
		
		walletInfo = {
			"name": key,
			"address": '0x' + wallet.address.toLowerCase()
		};
		
		walletList.push(walletInfo);
	}
	return JSON.stringify(walletList);
}

/**
 * 设置初始账户，并预分配固定比例Token，合约仅支持3个初始账户。仅合约owner有权限执行 
 * 
 * @method setInitAccount
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 合约执行的发起账户
 * 			{initAccount} 交易费率，合约控制交易费率在1.00%~3.00%之间
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.setInitAccount = function(walletName, password, executeAccount, initAccount, callback){
	var formatedData;
    var rawTx;
    var privateKey;

    var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
    //判断合约发起账户是不是owner账户
	if(web3.utils.isAddress(executeAccount)){
		if(executeAccount.toLowerCase().localeCompare(_this.contractOwner.toLowerCase()) != 0){
            console.log("error, it's not Owner");
            return false;
        }
    }else{
        console.log("invalid account");
        return false;
    }

	if(!web3.utils.isAddress(initAccount)){
		console.log("invalid initial account");
		return false;
	}
	
    //通过钱包名称、口令和账户找到私钥信息
    privateKey = _this.loadWallet(walletName, password, executeAccount);
    if(typeof privateKey == 'undefined'){
        console.log("don't find valid wallet");
        return false;
    }
    
    //格式化合约执行数据
    formatedData = web3.eth.abi.encodeFunctionSignature('setInitAccount(address)');
    formatedData += web3.eth.abi.encodeParameter('address', initAccount).slice(2);
    rawTx = {
        from: executeAccount,
        to: _this.contractAccount,
        value: '0x00',
        data: formatedData
    };
    
    //评估合约执行代码的gas量和获取账户nonce
    var pEstimateGas = web3.eth.estimateGas(rawTx);
    var pNonce = web3.eth.getTransactionCount(executeAccount);
    Promise.all([pEstimateGas, pNonce]).then(function(value){
        var gasLimit = value[0];
		var nonce = value[1];
        
        rawTx = {
            nonce: nonce,
            to: _this.contractAccount,
            value: '0x00',
            gasPrice: _this.gasPrice,
            gasLimit: gasLimit*10, //why estimate isn't correct
            data: formatedData,
            chainId: _this.chainID
        };

        //使用私钥进行签名并发送交易
        var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
        Promise.all([pSign]).then(function(result){
            var signedData = result[0];
            var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
            Promise.all([pSend]).then(function(result){
                var receipt = result[0];
                if(typeof callback != 'undefined'){
                    callback(null, receipt.transactionHash);
                }
            });
        });
    });
    return true;
}

/**
 * 设置交易费率，仅合约owner有权限设置 
 * 
 * @method setRate 
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 合约执行的发起账户
 * 			{rating} 交易费率，合约控制交易费率在1.00%~3.00%之间
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.setRate = function(walletName, password, executeAccount, rating, callback){
    var rate;
    var formatedData;
    var rawTx;
    var privateKey;

    var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	if(rating > 3.00 || rating < 1.00){
        console.log("invalid rate");
		return false;	
	}
	
    //判断合约发起账户是不是owner账户
	if(web3.utils.isAddress(executeAccount)){
		if(executeAccount.toLowerCase().localeCompare(_this.contractOwner.toLowerCase()) != 0){
            console.log("error, it's not Owner");
            return false;
        }
    }else{
        console.log("invalid account");
        return false;
    }

    //由于solidity不支持浮点运算，所以外部乘以100代入运算
    rate = parseInt(rating*100);
        
    //通过钱包名称、口令和账户找到私钥信息
    privateKey = _this.loadWallet(walletName, password, executeAccount);
    if(typeof privateKey == 'undefined'){
        console.log("don't find valid wallet");
        return false;
    }
    
    //格式化合约执行数据
    formatedData = web3.eth.abi.encodeFunctionSignature('setRate(uint256)');
    formatedData += web3.eth.abi.encodeParameter('uint256', rate).slice(2);
    rawTx = {
        from: executeAccount,
        to: _this.contractAccount,
        value: '0x00',
        data: formatedData
    };
    
    //评估合约执行代码的gas量和获取账户nonce
    var pEstimateGas = web3.eth.estimateGas(rawTx);
    var pNonce = web3.eth.getTransactionCount(executeAccount);
    Promise.all([pEstimateGas, pNonce]).then(function(value){
        var gasLimit = value[0];
		var nonce = value[1];
        
        rawTx = {
            nonce: nonce,
            to: _this.contractAccount,
            value: '0x00',
            gasPrice: _this.gasPrice,
            gasLimit: gasLimit*10, //why estimate isn't correct
            data: formatedData,
            chainId: _this.chainID
        };

        //使用私钥进行签名并发送交易
        var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
        Promise.all([pSign]).then(function(result){
            var signedData = result[0];
            var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
            Promise.all([pSend]).then(function(result){
                var receipt = result[0];
                if(typeof callback != 'undefined'){
                    callback(null, receipt.transactionHash);
                }
            });
        });
    });
    return true;
}

/**
 * 获取交易费率
 * 
 * @method getRate 
 * @param {callback(error, result)} 回调返回函数，正常情况下error为null，result返回交易费率
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.getRate = function(callback){
	var formatedData, rawTx;
	var _this = this;
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	//检查callback函数
	if(typeof callback == 'undefined'){
		console.log("error, please input callback function");
		return false;
	}

	//格式化message数据	
	formatedData = web3.eth.abi.encodeFunctionSignature('getRate()');
	rawTx = {
		to: this.contractAccount,
		data: formatedData
	};
	
	//发起查询message
    var pCall = web3.eth.call(rawTx);
    Promise.all([pCall]).then(function(value){
		var rate = value[0];
        rate = web3.utils.hexToNumber(rate)/100.00;
		callback(null, rate);
	});
	return true;
}

/**
 * 设置用户管理账户，仅合约owner有权限设置 
 * 
 * @method setUserMaster
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 合约执行的发起账户
 * 			{userMaster} 用户注册管理账户 
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.setUserMaster = function(walletName, password, executeAccount, userMaster, callback){
	var formatedData;
	var rawTx;
	var privateKey;
	
	var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}

	if(!web3.utils.isAddress(userMaster)){
		console.log("invalid account");
		return false;
	}
	
    //判断合约发起账户是不是owner账户
	if(web3.utils.isAddress(executeAccount)){
		if(executeAccount.toLowerCase().localeCompare(_this.contractOwner.toLowerCase()) != 0){
            console.log("error, it's not Owner");
            return false;
        }
    }else{
        console.log("invalid execute account");
        return false;
    }

    //通过钱包名称、口令和账户找到私钥信息
	privateKey = _this.loadWallet(walletName, password, executeAccount);
	if(typeof privateKey == 'undefined'){
		console.log("don't find valid wallet");
		return false;
	}
   	
	//格式化合约执行数据
	formatedData = web3.eth.abi.encodeFunctionSignature('setUserMaster(address)');
	formatedData += web3.eth.abi.encodeParameter('address', userMaster).slice(2);
    rawTx = {
        from: executeAccount,
        to: _this.contractAccount,
        value: '0x00',
        data: formatedData
    };
	
	//评估合约执行代码的gas量和获取账户nonce
    var pEstimateGas = web3.eth.estimateGas(rawTx);
    var pNonce = web3.eth.getTransactionCount(executeAccount);
    Promise.all([pEstimateGas, pNonce]).then(function(value){
		var gasLimit = value[0];
		var nonce = value[1];
		var rawTx;
		
        rawTx = {
            nonce: nonce,
            to: _this.contractAccount,
            value: '0x00',
            gasPrice: _this.gasPrice,
            gasLimit: gasLimit*10, //why estimate isn't correct
            data: formatedData,
            chainId: _this.chainID
        };

		//使用私钥进行签名并发送交易
        var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
        Promise.all([pSign]).then(function(result){
            var signedData = result[0];
            var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
            Promise.all([pSend]).then(function(result){
				var receipt = result[0];
                if(typeof callback != 'undefined'){
                    callback(null, receipt.transactionHash);
                }
            });
        });
	});
	
    return true;
}

/**
 * 获取交易费接收账户信息
 * 
 * @method getUserMaster
 * @param {callback(error, result)} 回调返回函数，正常情况下error为null，result返回交易费接收账户信息
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.getUserMaster = function(callback){
	var _this = this;
	var formatedData, rawTx;

	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	//检查callback函数
	if(typeof callback == 'undefined'){
		console.log("error, please input callback function");
		return false;
	}

	//格式化message数据	
	formatedData = web3.eth.abi.encodeFunctionSignature('getUserMaster()');
	rawTx = {
		to: this.contractAccount,
		data: formatedData
	};
	
	//发起查询message并通过回调函数返回查询数据
	var pCall = web3.eth.call(rawTx);
    Promise.all([pCall]).then(function(result){
        var address;
        var userMaster = result[0];
        address = "0x" + userMaster.slice(26);
        callback(null, address);
	});

	return true;
}

/**
 * 设置交易费接收账户，仅合约owner有权限设置 
 * 
 * @method setFeeAccount
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 合约执行的发起账户
 * 			{feeAccount} 交易费接收账户 
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.setFeeAccount = function(walletName, password, executeAccount, feeAccount, callback){
	var formatedData;
	var rawTx;
	var privateKey;
	
	var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}

	if(!web3.utils.isAddress(feeAccount)){
		console.log("invalid fee account");
		return false;
	}
	
    //判断合约发起账户是不是owner账户
	if(web3.utils.isAddress(executeAccount)){
		if(executeAccount.toLowerCase().localeCompare(_this.contractOwner.toLowerCase()) != 0){
            console.log("error, it's not Owner");
            return false;
        }
    }else{
        console.log("invalid execute account");
        return false;
    }

    //通过钱包名称、口令和账户找到私钥信息
	privateKey = _this.loadWallet(walletName, password, executeAccount);
	if(typeof privateKey == 'undefined'){
		console.log("don't find valid wallet");
		return false;
	}
   	
	//格式化合约执行数据
	formatedData = web3.eth.abi.encodeFunctionSignature('setFeeAccount(address)');
	formatedData += web3.eth.abi.encodeParameter('address', feeAccount).slice(2);
    rawTx = {
        from: executeAccount,
        to: _this.contractAccount,
        value: '0x00',
        data: formatedData
    };
	
	//评估合约执行代码的gas量和获取账户nonce
    var pEstimateGas = web3.eth.estimateGas(rawTx);
    var pNonce = web3.eth.getTransactionCount(executeAccount);
    Promise.all([pEstimateGas, pNonce]).then(function(value){
		var gasLimit = value[0];
		var nonce= value[1];
		var rawTx;
		
        rawTx = {
            nonce: nonce,
            to: _this.contractAccount,
            value: '0x00',
            gasPrice: _this.gasPrice,
            gasLimit: gasLimit*10, //why estimate isn't correct
            data: formatedData,
            chainId: _this.chainID
        };

		//使用私钥进行签名并发送交易
        var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
        Promise.all([pSign]).then(function(result){
            var signedData = result[0];
            var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
            Promise.all([pSend]).then(function(result){
                var receipt = result[0];
                if(typeof callback != 'undefined'){
                    callback(null, receipt.transactionHash);
                }
            });
        });
	});
    return true;
}

/**
 * 获取交易费接收账户信息
 * 
 * @method getFeeAccount
 * @param {callback(error, result)} 回调返回函数，正常情况下error为null，result返回交易费接收账户信息
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.getFeeAccount = function(callback){
	var _this = this;
	var formatedData, rawTx;

	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	//检查callback函数
	if(typeof callback == 'undefined'){
		console.log("error, please input callback function");
		return false;
	}

	//格式化message数据	
	formatedData = web3.eth.abi.encodeFunctionSignature('getFeeAccount()');
	rawTx = {
		to: this.contractAccount,
		data: formatedData
	};
	
	//发起查询message并通过回调函数返回查询数据
	var pCall = web3.eth.call(rawTx);
    Promise.all([pCall]).then(function(result){
        var address;
        var feeAccount = result[0];
        address = "0x" + feeAccount.slice(26);
        callback(null, address);
	});

	return true;
}

/**
 * 注册用户账户信息，仅注册用户才享有奖励机制，仅用户注册管理账户有权限设置 
 * 
 * @method resigterUser 
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 合约执行的发起账户
 * 			{user} 注册用户账号信息 
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.registerUser = function(walletName, password, executeAccount, user, callback){
    var formatedData;
    var rawTx;
    var privateKey;

    var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	if(web3.utils.isAddress(executeAccount)){
        //格式化message数据	
        formatedData = web3.eth.abi.encodeFunctionSignature('getUserMaster()');
        rawTx = {
            to: _this.contractAccount,
            data: formatedData
        };
        
        //发起查询message并通过回调函数返回查询数据
        var pCall = web3.eth.call(rawTx);
        Promise.all([pCall]).then(function(result){
            var address;
            var userMaster = result[0];
            address = "0x" + userMaster.slice(26);

            //判断合约发起账户是不是master账户
            if(executeAccount.toLowerCase().localeCompare(address.toLowerCase()) != 0){
            	console.log("error, it's not user master");
            	return false;
        	}

            //检查注册用户账号信息
            if(!web3.utils.isAddress(user)){
                console.log("invalid user account");
                return false;
            }

            //通过钱包名称、口令和账户找到私钥信息
            privateKey = _this.loadWallet(walletName, password, executeAccount);
            if(typeof privateKey == 'undefined'){
                console.log("don't find valid wallet");
                return false;
            }
            
            //格式化合约执行数据
            formatedData = web3.eth.abi.encodeFunctionSignature('registerUser(address)');
            formatedData += web3.eth.abi.encodeParameter('address', user).slice(2);
            rawTx = {
                from: executeAccount,
                to: _this.contractAccount,
                value: '0x00',
                data: formatedData
            };
            
            //评估合约执行代码的gas量和获取账户nonce
            var pEstimateGas = web3.eth.estimateGas(rawTx);
            var pNonce = web3.eth.getTransactionCount(executeAccount);
            Promise.all([pEstimateGas, pNonce]).then(function(value){
                var gasLimit = value[0];
				var nonce = value[1];
                
                rawTx = {
                    nonce: nonce,
                    to: _this.contractAccount,
                    value: '0x00',
                    gasPrice: _this.gasPrice,
                    gasLimit: gasLimit*10, //why estimate isn't correct
                    data: formatedData,
                    chainId: _this.chainID
                };

                //使用私钥进行签名并发送交易
                var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
                Promise.all([pSign]).then(function(result){
                    var signedData = result[0];
                    var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
                    Promise.all([pSend]).then(function(result){
                        var receipt = result[0];
                        if(typeof callback != 'undefined'){
                            callback(null, receipt.transactionHash);
                        }
                    });
                });
            });
            return true;
        });

    }else{
        console.log("invalid execute account");
        return false;
    }
}

/**
 * 检查指定用户是否注册 
 * 
 * @method checkUser
 * @param  {user} 用户账户信息
 * 		   {callback(error, result)} 回调返回函数，正常情况下error为null，result返回查找结果(true/false)
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.checkUser = function(user, callback){
	var _this = this;
	var formatedData, rawTx;

	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}

	if(!web3.utils.isAddress(user)){
		console.log("invalid user account");
		return false;
	}

	//检查callback函数
	if(typeof callback == 'undefined'){
		console.log("error, please input callback function");
		return false;
	}

	//格式化message数据	
	formatedData = web3.eth.abi.encodeFunctionSignature('checkUser(address)');
	formatedData += web3.eth.abi.encodeParameter('address', user).slice(2);
	rawTx = {
		to: this.contractAccount,
		data: formatedData
	};
	
	//发起查询message并通过回调函数返回查询结果
	var pCall = web3.eth.call(rawTx);
    Promise.all([pCall]).then(function(result){
        var value = result[0];
		value = web3.utils.hexToNumber(value);
        callback(null, value);
	});

	return true;
}

/**
 * 请求系统对执行账户进行Token奖励，系统奖励数量一次不得超过100Token，小于1Token
 * 
 * @method  exchangeToken 
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 合约执行的发起账户
 * 			{bonus} 奖金数量，输入控制在1~100Token之间
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.exchangeToken = function(walletName, password, executeAccount, bonus, callback){
    var value;
    var formatedData;
    var rawTx;
    var privateKey;

    var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	//检查合约执行账户有效性
	if(!web3.utils.isAddress(executeAccount)){
		console.log("invalid execute account");
		return false
	}
	
	if(bonus < 1.0 || bonus > 100.0){
		console.log("invalid bonus");
		return false;
	}

	
    //通过钱包名称、口令和账户找到私钥信息
    privateKey = _this.loadWallet(walletName, password, executeAccount);
    if(typeof privateKey == 'undefined'){
        console.log("don't find valid wallet");
        return false;
    }
    
    //格式化合约执行数据
	value = bonus*Math.pow(10, _this.tokenDecimals);
	value = value.toFixed();
	value = web3.utils.toHex(value);
	formatedData = web3.eth.abi.encodeFunctionSignature('exchangeToken(uint256)');
    formatedData += web3.eth.abi.encodeParameter('uint256', value).slice(2);
    rawTx = {
        from: executeAccount,
        to: _this.contractAccount,
        value: '0x00',
        data: formatedData
    };
    
    //评估合约执行代码的gas量和获取账户nonce
    var pEstimateGas = web3.eth.estimateGas(rawTx);
    var pNonce = web3.eth.getTransactionCount(executeAccount);
    Promise.all([pEstimateGas, pNonce]).then(function(value){
        var gasLimit = value[0];
		var nonce = value[1];
        
        rawTx = {
            nonce: nonce,
            to: _this.contractAccount,
            value: '0x00',
            gasPrice: _this.gasPrice,
            gasLimit: gasLimit*10, //why estimate isn't correct
            data: formatedData,
            chainId: _this.chainID
        };

        //使用私钥进行签名并发送交易
        var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
        Promise.all([pSign]).then(function(result){
            var signedData = result[0];
            var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
            Promise.all([pSend]).then(function(result){
                var receipt = result[0];
                if(typeof callback != 'undefined'){
                    callback(null, receipt.transactionHash);
                }
            });
        });
    });
    return true;
}

/**
 * 执行带手续费的转账交易 
 * 
 * @method  transferByFee
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 账户发起账户 
 * 			{toAccount} 转账接收账户
 * 			{amount} 转账金额
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.transferByFee = function(walletName, password, executeAccount, toAccount, amount, callback){
	var value;
	var formatedData;
    var rawTx;
    var privateKey;

    var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	//检查合约执行账户有效性
	if(!web3.utils.isAddress(executeAccount) || !web3.utils.isAddress(toAccount)){
		console.log("invalid input account");
		return false;
	}
	
    //通过钱包名称、口令和账户找到私钥信息
    privateKey = _this.loadWallet(walletName, password, executeAccount);
    if(typeof privateKey == 'undefined'){
        console.log("don't find valid wallet");
        return false;
    }
    
    //格式化合约执行数据
	value = amount*Math.pow(10, _this.tokenDecimals);
	value = value.toFixed();
	value = web3.utils.toHex(value);
	formatedData = web3.eth.abi.encodeFunctionSignature('transferByFee(address,uint256)');
    formatedData += web3.eth.abi.encodeParameter('address', toAccount).slice(2);
    formatedData += web3.eth.abi.encodeParameter('uint256', value).slice(2);
    rawTx = {
        from: executeAccount,
        to: _this.contractAccount,
        value: '0x00',
        data: formatedData
    };
    
    //评估合约执行代码的gas量和获取账户nonce
    var pEstimateGas = web3.eth.estimateGas(rawTx);
    var pNonce = web3.eth.getTransactionCount(executeAccount);
    Promise.all([pEstimateGas, pNonce]).then(function(value){
        var gasLimit = value[0]; 
		var nonce = value[1];
        
        rawTx = {
            nonce: nonce,
            to: _this.contractAccount,
            value: '0x00',
            gasPrice: _this.gasPrice,
            gasLimit: gasLimit*10, //why estimate isn't correct
            data: formatedData,
            chainId: _this.chainID
        };

        //使用私钥进行签名并发送交易
        var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
        Promise.all([pSign]).then(function(result){
            var signedData = result[0];
            var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
            Promise.all([pSend]).then(function(result){
                var receipt = result[0];
                if(typeof callback != 'undefined'){
                    callback(null, receipt.transactionHash);
                }
            });
        });
    });
    return true;
}

/**
 * 执行不带手续费的转账交易 
 * 
 * @method  transfer
 * @param 	{wallName} 钱包名称，通过walletName和executeAccount来确定判断钱包里面账户正确性
 * 			{password} 账户访问口令
 * 			{executeAccount} 转账发起账户 
 * 			{toAccount} 转账接收账户
 * 			{amount} 转账金额
 * 			{callback(error, result)} 可选，回调返回函数，正常情况下error为null，result返回该交易hash值
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.transfer = function(walletName, password, executeAccount, toAccount, amount, callback){
	var value;
	var formatedData;
    var rawTx;
    var privateKey;

    var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	//检查合约执行账户有效性
	if(!web3.utils.isAddress(executeAccount) || !web3.utils.isAddress(toAccount)){
		console.log("invalid input account");
		return false
	}
	
    //通过钱包名称、口令和账户找到私钥信息
    privateKey = _this.loadWallet(walletName, password, executeAccount);
    if(typeof privateKey == 'undefined'){
        console.log("don't find valid wallet");
        return false;
    }
    
    //格式化合约执行数据
	value = amount*Math.pow(10, _this.tokenDecimals);
	value = value.toFixed();
	value = web3.utils.toHex(value);
	formatedData = web3.eth.abi.encodeFunctionSignature('transfer(address,uint256)');
    formatedData += web3.eth.abi.encodeParameter('address', toAccount).slice(2);
    formatedData += web3.eth.abi.encodeParameter('uint256', value).slice(2);
    rawTx = {
        from: executeAccount,
        to: _this.contractAccount,
        value: '0x00',
        data: formatedData
    };
    
    //评估合约执行代码的gas量和获取账户nonce
    var pEstimateGas = web3.eth.estimateGas(rawTx);
    var pNonce = web3.eth.getTransactionCount(executeAccount);
    Promise.all([pEstimateGas, pNonce]).then(function(value){
		var gasLimit = value[0]; 
		var nonce = value[1];
        
        rawTx = {
            nonce: nonce,
            to: _this.contractAccount,
            value: '0x00',
            gasPrice: _this.gasPrice,
            gasLimit: gasLimit*10, //why estimate isn't correct
            data: formatedData,
            chainId: _this.chainID
        };

        //使用私钥进行签名并发送交易
        var pSign = web3.eth.accounts.signTransaction(rawTx, privateKey);
        Promise.all([pSign]).then(function(result){
            var signedData = result[0];
            var pSend = web3.eth.sendSignedTransaction(signedData.rawTransaction);
            Promise.all([pSend]).then(function(result){
                var receipt = result[0];
                if(typeof callback != 'undefined'){
                    callback(null, receipt.transactionHash);
                }
            });
        });
    });
    return true;
}

/**
 * 查询指定账户余额
 * 
 * @method balanceOf 
 * @param  {target} 目标账户信息
 * 		   {callback(error, result)} 回调返回函数，正常情况下error为null，result返回查找结果
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.balanceOf = function(target, callback){

	var formatedData, rawTx;
	var _this = this;
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}

	if(!web3.utils.isAddress(target)){
		console.log("invalid target account");
		return false;
	}

	//检查callback函数
	if(typeof callback == 'undefined'){
		console.log("error, please input callback function");
		return false;
	}

	//格式化message数据	
	formatedData = web3.eth.abi.encodeFunctionSignature('balanceOf(address)');
	formatedData += web3.eth.abi.encodeParameter('address', target).slice(2);
	rawTx = {
		to: this.contractAccount,
		data: formatedData
	};
	
	//发起查询message并通过回调函数返回查询结果
	var pCall = web3.eth.call(rawTx);
    Promise.all([pCall]).then(function(result){
        var value, high, low;
		var value = result[0];
		//由于javascript中小于10e53 - 1为安全数字，所以需要折半处理
		//如果这里的高位数字大于10e53，则还需要进一步折半处理
		high = web3.utils.hexToNumber(value.slice(0, 54)); //取高256-48位
		low = '0x' + value.slice(54, 66); 
		low = web3.utils.hexToNumber(low); //取低48位
		value = low + high*0x1000000000000;

		value = value / (1.0*Math.pow(10, _this.tokenDecimals));
		value = value.toFixed(_this.tokenDecimals);
		callback(null, value);
	});

	return true;
}

/**
 * 查询系统资金余额，仅允许owner有权限
 * 
 * @method balanceOwner 
 * @param  {owner} 合约owner账户 
 * 		   {callback(error, result)} 回调返回函数，正常情况下error为null，result返回查找结果
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.balanceOwner = function(owner, callback){

	var formatedData, rawTx;
	var _this = this;

	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	//判断是不是owner账户
	if(web3.utils.isAddress(owner)){
		if(owner.toLowerCase().localeCompare(_this.contractOwner.toLowerCase()) != 0){
            console.log("error, it's not Owner");
            return false;
        }
    }else{
        console.log("invalid execute account");
        return false;
    }


	if(!web3.utils.isAddress(target)){
		console.log("invalid target account");
		return false;
	}

	//检查callback函数
	if(typeof callback == 'undefined'){
		console.log("error, please input callback function");
		return false;
	}

	//格式化message数据	
	formatedData = web3.eth.abi.encodeFunctionSignature('balanceOf(address)');
	formatedData += web3.eth.abi.encodeParameter('address', target).slice(2);
	rawTx = {
		to: this.contractAccount,
		data: formatedData
	};
	
	//发起查询message并通过回调函数返回查询结果
	var pCall = web3.eth.call(rawTx);
    Promise.all([pCall]).then(function(result){
        var value = result[0];
		value = web3.utils.hexToNumber(value) / (1.0*Math.pow(10, _this.tokenDecimals));
		value = value.toFixed(_this.tokenDecimals);
        callback(null, value);
	});

	return true;
}

/**
 * 查询指定账户所参与的交易记录
 * 
 * @method showHistoryTransaction
 * @param  {target} 目标账户 
 * 		   {callback(error, result)} 回调返回函数，正常情况下error为null。
 * 		   result返回查找结果，为数组类型，包含{from,to,value,date}。
 * @return 成功则返回true，失败则返回false
 */
CToken.prototype.showHistoryTransactions = function(target, callback){
	var opcode, extAddress;
	var pLogs1, pLogs2;
	var _this = this;
	
    var history = new Array();
	
	if(!_this.isConnected){
        console.log("connect network failed");
        return false;
	}
	
	if(!web3.utils.isAddress(target)){
		console.log("invalid target account");
		return false;
	}

	//检查callback函数
	if(typeof callback == 'undefined'){
		console.log("error, please input callback function");
		return false;
	}

	//生成opcode
	extAddress = web3.utils.leftPad(target, 64);
	opcode = web3.eth.abi.encodeEventSignature('TransferByTime(address,address,uint256,uint256)');
	
	//查询交易源事件信息
	pLogs1 = web3.eth.getPastLogs({
        address: _this.contractAccount, 
        topics: [opcode, extAddress, null],
        fromBlock: "earliest"  //??why can't input hex string
    });
	
	//查询交易目的事件信息
    pLogs2 = web3.eth.getPastLogs({
        address: _this.contractAccount, 
        topics: [opcode, null, extAddress],
        fromBlock: "earliest"  
    });

	//等待查询结果
	Promise.all([pLogs1, pLogs2]).then(function(value){
		var res1 = value[0];
		var res2 = value[1];

		//格式化源记录查询信息
		for(var i = 0; i < res1.length; i++){
            var result = {};
			var value, timestamp;
			var time = new Date();
			
			//解析日志信息源和目的
            result.from = '0x' + res1[i].topics[1].slice(26);
            result.to = '0x' + res1[i].topics[2].slice(26);
		
			//解析金额
			value = res1[i].data.slice(0,66);
            value = web3.utils.hexToNumber(value)/(1.0*Math.pow(10, _this.tokenDecimals));
			result.value = value.toFixed(_this.tokenDecimals);

			//解析时间
			timestamp = '0x' + res1[i].data.slice(67,131);
            timestamp = web3.utils.hexToNumber(timestamp);
			time.setTime(timestamp * 1000);
			result.time = time.toString();

            history.push(result);
        }

		//格式化目的记录查询信息
        for(var i = 0; i < res2.length; i++){
            var result = {};
			var value, timestamp;
			var time = new Date();
			
			//解析日志信息源和目的
            result.from = '0x' + res2[i].topics[1].slice(26);
            result.to = '0x' + res2[i].topics[2].slice(26);
		
			//解析金额
			value = res2[i].data.slice(0,66);
            value = web3.utils.hexToNumber(value)/(1.0*Math.pow(10, _this.tokenDecimals));
			result.value = value.toFixed(_this.tokenDecimals);

			//解析时间
			timestamp = '0x' + res2[i].data.slice(67,131);
            timestamp = web3.utils.hexToNumber(timestamp);
			time.setTime(timestamp * 1000);
			result.time = time.toString();

            history.push(result);
        }
        
        //返回查询信息
        callback(null, history);
    });

	return true;
}

/*
var subscription;

CToken.prototype.subscribe = function(target, callback){	
	var _this = this;

	//生成opcode
	var extAddress = web3.utils.leftPad(target, 64);
	var opcode = web3.eth.abi.encodeEventSignature('Transfer(address,address,uint256)');
	subscription = web3.eth.subscribe('logs', {
		fromBlock: 'latest',
		address: _this.contractAccount,
		topics: [opcode, null, extAddress]
	}, callback);	
}
*/
