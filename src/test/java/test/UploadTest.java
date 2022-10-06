/**
 * Create by xy
 * 2022-05-11 17:35
 */

package test;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class UploadTest {
    @Test
    public void test1(){
        String fileName = "erer.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
    @Test
    public void test2(){
        Date date = new Date(System.currentTimeMillis());
        System.out.println(date);
    }
/*
@RequestMapping(value = "/page",method = RequestMethod.GET)
    public R<Page> page(int page, int pageSize,String number,String beginTime,String endTime){
        logger.info("分页查询入参{}--{}-{}-{}-{}",pageSize,page,number,beginTime,endTime);
        try{

            OrdersPageReqDto ordersPageReqDto = new OrdersPageReqDto();
            ordersPageReqDto.setNumber(number);
            ordersPageReqDto.setBeginTime(beginTime);
            ordersPageReqDto.setEndTime(endTime);

            ordersPageReqDto.setPage(page);
            ordersPageReqDto.setPageSize(pageSize);


            Page<OrdersPageRspDto> pagess = ordersService.pagess(ordersPageReqDto);
            logger.info("分页查询出参{}",JSONObject.toJSONString(pagess));

            return  R.success(pagess);

        }catch (Exception e){
            logger.info("分页查询异常{}",e);
            return R.error("分页查询异常");
        }
    }

 */




    /*
    @GetMapping("/page")
    public R<Page<OrdersDto>> page(int page, int pageSize, String number,
                              Date beginTime,
                              Date endTime) {
        LocalDateTime localDateTimeBegin = null;
        LocalDateTime localDateTimeEnd = null;
        // 对其时间参数进行处理
        if (beginTime != null && endTime != null) {
            // beginTime处理
            Instant instant = beginTime.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            localDateTimeBegin = instant.atZone(zoneId).toLocalDateTime();
            //formatBeginTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // endTime 进行处理
            Instant instant1 = endTime.toInstant();
            ZoneId zoneId1 = ZoneId.systemDefault();
            localDateTimeEnd = instant1.atZone(zoneId1).toLocalDateTime();
            //formatEndTime = localDateTime1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        Page<Salary> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> pageDto = new Page<>();

        QueryWrapper<Salary> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(number)) {
            wrapper.eq("number", number);
        }
        if (!StringUtils.isEmpty(localDateTimeBegin)) {
            wrapper.ge("order_time", localDateTimeBegin);
        }
        if (!StringUtils.isEmpty(localDateTimeEnd)) {
            wrapper.le("order_time", localDateTimeEnd);
        }
        wrapper.orderByDesc("order_time");
        ordersService.page(pageInfo, wrapper);
        // 将其除了records中的内存复制到pageDto中
        BeanUtils.copyProperties(pageInfo, pageDto, "records");

        List<Salary> records = pageInfo.getRecords();

        List<OrdersDto> collect = records.stream().map((order) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(order, ordersDto);
            // 根据订单id查询订单详细信息

            QueryWrapper<OrderDetail> wrapperDetail = new QueryWrapper<>();
            wrapperDetail.eq("order_id", order.getId());

            List<OrderDetail> orderDetails = orderDetailService.list(wrapperDetail);
            ordersDto.setOrderDetails(orderDetails);

            // 根据userId 查询用户姓名
            Long userId = order.getUserId();
            User user = userService.getById(userId);
            ordersDto.setUserName(user.getName());
            ordersDto.setPhone(user.getPhone());

            // 获取地址信息
            Long addressBookId = order.getAddressBookId();
            AddressBook addressBook = addressBookService.getById(addressBookId);
            ordersDto.setAddress(addressBook.getDetail());
            ordersDto.setConsignee(addressBook.getConsignee());

            return ordersDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(collect);

        return R.success(pageDto);
    }


     */
}
